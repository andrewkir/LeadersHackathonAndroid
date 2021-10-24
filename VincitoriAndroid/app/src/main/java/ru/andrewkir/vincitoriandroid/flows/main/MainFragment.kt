package ru.andrewkir.vincitoriandroid.flows.main

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable.createFromAttributes
import com.google.android.material.chip.ChipGroup
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.ui_view.ViewProvider
import ru.andrewkir.vincitoriandroid.BuildConfig.API_KEY
import ru.andrewkir.vincitoriandroid.R
import ru.andrewkir.vincitoriandroid.common.BaseFragment
import ru.andrewkir.vincitoriandroid.common.dp
import ru.andrewkir.vincitoriandroid.common.px
import ru.andrewkir.vincitoriandroid.databinding.FragmentMainBinding
import ru.andrewkir.vincitoriandroid.web.service.ApiService


class MainFragment :
    BaseFragment<MainViewModel, MainRepository, FragmentMainBinding>(), GeoObjectTapListener,
    CameraListener {

    private lateinit var recyclerAdapter: LegendAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    lateinit var cameraPosition: CameraPosition

    override fun provideViewModelClass() = MainViewModel::class.java

    override fun provideRepository(): MainRepository {
        return MainRepository(
            apiProvider.provideApi(
                ApiService::class.java,
            )
        )
    }

    override fun provideBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding =
        FragmentMainBinding.inflate(inflater, container, false)

    lateinit var fusedLocationClient: FusedLocationProviderClient

    var animationHandler: Handler? = null

    var choosedFilters = hashMapOf<String, MutableList<Int>>()

    private val MAPKIT_API_KEY = API_KEY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            if (viewModel.isApiSet) {
                MapKitFactory.setApiKey(MAPKIT_API_KEY)
            }
        } catch (e: UninitializedPropertyAccessException) {
            try {
                MapKitFactory.setApiKey(MAPKIT_API_KEY)
            } catch (e: AssertionError) {
            }
        }
        MapKitFactory.initialize(requireContext())
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isApiSet = true

        requestRequiredPermissions()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        bind.mapview.map.addCameraListener(this)

        observeHeatMap()
        observeFilters()
        observeObjects()
        observeAttributes()
        observeSportsZoneHeatMap()
        observeStatistics()

        setupRecyclerView()

        bind.bottomLayout.closeBottomButton.setOnClickListener {
            bind.bottomLayout.bottomSheet.visibility = View.GONE
            val mBottomSheetBehavior = BottomSheetBehavior.from(bind.bottomLayout.bottomSheet)
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.getFilters()

        bind.search.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (bind.filtersLayout.visibility == View.VISIBLE) {
                        return onQueryTextChange(query)
                    }
                    viewModel.getObjects(bind.search.query.toString(), choosedFilters)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrEmpty()) {
                        if (bind.filtersLayout.visibility == View.VISIBLE) {
                            for (chipGroup in bind.filtersLayout.children) {
                                if (chipGroup is ChipGroup) {
                                    for (chip in chipGroup.children) {
                                        if (!(chip as Chip).text.toString().lowercase()
                                                .contains(bind.search.query.toString().lowercase())
                                        ) {
                                            if (chip.text.toString() != "Сбросить") chip.visibility =
                                                View.GONE
                                        } else {
                                            chip.visibility = View.VISIBLE
                                            chipGroup.visibility = View.VISIBLE
                                        }
                                    }
                                }
                            }
                        } else {
                            return true
                        }
                    } else {
                        for (chipGroup in bind.filtersLayout.children) {
                            if (chipGroup is ChipGroup) {
                                for (chip in chipGroup.children) {
                                    chip.visibility = View.VISIBLE
                                }
                                chipGroup.visibility = View.GONE
                            }
                        }
                    }
                    return true
                }
            }
        )

        bind.infoButton.setOnClickListener {
            bind.legendView.visibility =
                if (bind.legendView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        bind.layersButton.setOnClickListener {
            bind.layersView.visibility =
                if (bind.layersView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        bind.search.queryHint = "Поиск спортивного объекта"
        bind.search.isIconified = false
        bind.search.isFocusable = false
        bind.search.isIconified = false
        bind.search.clearFocus()

        bind.filterButton.setOnClickListener {
            if (bind.filtersLayout.visibility == View.VISIBLE) {
                bind.filtersLayout.visibility = View.GONE
                bind.search.setQuery("", false)
                bind.search.queryHint = "Поиск спортивного объекта"
            } else {
                bind.filtersLayout.visibility = View.VISIBLE
                bind.search.setQuery("", false)
                bind.search.queryHint = "Поиск фильтра"
            }
        }

        bind.bottomLayout.bottomSheet.visibility = View.GONE
        val mBottomSheetBehavior = BottomSheetBehavior.from(bind.bottomLayout.bottomSheet)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        configureSearchButton(bind.search)
        configureCloseButton(bind.search)

        viewModel.getStatistics()

        bind.firstHeatMap.setOnClickListener {
            bind.progressBar.visibility = View.VISIBLE
            bind.loadingBackground.visibility = View.VISIBLE
            viewModel.getStatistics()
            viewModel.getHeatMap()
        }

        bind.secondHeatMap.setOnClickListener {
            bind.progressBar.visibility = View.VISIBLE
            bind.loadingBackground.visibility = View.VISIBLE
            viewModel.getStatistics()
            viewModel.getSportZonesHeatMap(choosedFilters)
        }

        bind.sportFacilities.setOnClickListener {
            bind.progressBar.visibility = View.VISIBLE
            bind.loadingBackground.visibility = View.VISIBLE
            viewModel.getObjects(bind.search.query.toString(), choosedFilters)
        }

        bind.clearButton.setOnClickListener {
            bind.mapview.map.mapObjects.clear()
        }

        bind.recommendations.setOnClickListener {
            Toast.makeText(requireContext(), "Пока в разработке :(", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                requireContext(),
                "Для работы функции определения геопозиции - приложению необходимо получить разрешения",
                Toast.LENGTH_LONG
            ).show()
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(
                    requireContext(),
                    "Без разрешения приложение не сможет получить Вашу текущую геопозицию!",
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(
                    requireContext(),
                    "Измените разрешения в настройках приложения для работы функции определения геопозиции",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    0
                )
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val task = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (!::cameraPosition.isInitialized) {
                            bind.mapview.map.move(
                                CameraPosition(
                                    Point(location!!.latitude, location.longitude),
                                    10.0f,
                                    0.0f,
                                    0.0f
                                )
                            )
                        }
                    }
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), 0)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::cameraPosition.isInitialized) {
            outState.putDouble("CAMERA_LAT", cameraPosition.target.latitude)
            outState.putDouble("CAMERA_LONG", cameraPosition.target.longitude)
            outState.putFloat("CAMERA_ZOOM", cameraPosition.zoom)
            outState.putFloat("CAMERA_TILT", cameraPosition.tilt)
            outState.putFloat("CAMERA_AZIMUTH", cameraPosition.azimuth)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        bind.bottomLayout.bottomSheet.visibility = View.GONE
        val mBottomSheetBehavior = BottomSheetBehavior.from(bind.bottomLayout.bottomSheet)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        savedInstanceState?.let {
            cameraPosition = CameraPosition(
                Point(
                    it.getDouble("CAMERA_LAT", 0.0),
                    it.getDouble("CAMERA_LONG", 0.0)
                ),
                it.getFloat("CAMERA_ZOOM", 10f),
                it.getFloat("CAMERA_AZIMUTH", 0f),
                it.getFloat("CAMERA_TILT", 0f)
            )
            bind.mapview.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom,
                    cameraPosition.azimuth,
                    cameraPosition.tilt
                )
            )
            bind.progressBar.visibility = View.GONE
            bind.loadingBackground.visibility = View.GONE
        }
    }

    private fun observeAttributes() {
        viewModel.attributes.observe(
            viewLifecycleOwner
        ) {
            bind.bottomLayout.run {
                titleText.text = it.name ?: ""
                descriptionText.text = it.sportzones!!.map { sportzone ->
                    sportzone.name
                }.joinToString(", ")
                addressText.text = it.address ?: ""
                depText.text = it.department ?: ""
                typeText.text = it.sportzones!!.map { sportzone ->
                    sportzone.name
                }.joinToString(", ")
                availableText.text = it.proximity ?: ""
            }
        }
    }

    private fun observeObjects() {
        viewModel.mapObjects.observe(
            viewLifecycleOwner
        ) {
            for (obj in it) {
                val imageView = ImageView(requireContext())
                imageView.setImageResource(R.drawable.ic_place_filled)
                val imageProvider = ViewProvider(imageView)
                bind.mapview.map.mapObjects.addPlacemark(
                    Point(obj.lat!!, obj.lng!!), imageProvider
                )
                val circle: CircleMapObject =
                    bind.mapview.map.mapObjects.addCircle(
                        Circle(Point(obj.lat!!, obj.lng!!), obj.radius!!.toFloat()),
                        10,
                        10f,
                        20
                    )
                val hexColor = java.lang.String.format(
                    "#%06X",
                    0xFFFFFF and obj.color!!
                )
                circle.fillColor =
                    Color.parseColor("#80${hexColor.slice(1 until hexColor.length)}")
                circle.strokeColor = Color.TRANSPARENT


                val circle2: CircleMapObject = bind.mapview.map.mapObjects.addCircle(
                    Circle(Point(obj.lat!! - 0.000001, obj.lng!!), 20.px.toFloat()),
                    Color.TRANSPARENT,
                    2f,
                    Color.TRANSPARENT
                )
                circle2.zIndex = 100.0f
                circle2.userData =
                    CircleMapObjectUserData(obj.id!!, obj.name!!)
                circle2.addTapListener(circleMapObjectTapListener)
            }
            bind.progressBar.visibility = View.GONE
            bind.loadingBackground.visibility = View.GONE
        }
    }

    private fun configureCloseButton(searchView: androidx.appcompat.widget.SearchView) {
        val searchClose = searchView.javaClass.getDeclaredField("mCloseButton")
        searchClose.isAccessible = true
        val closeImage = searchClose.get(searchView) as ImageView
        closeImage.setImageResource(R.drawable.close_background)
    }

    private fun configureSearchButton(searchView: androidx.appcompat.widget.SearchView) {
        val searchClose = searchView.javaClass.getDeclaredField("mSearchButton")
        searchClose.isAccessible = true
        val closeImage = searchClose.get(searchView) as ImageView
        closeImage.setImageResource(R.drawable.ic_search)
    }

    override fun onStop() {
        super.onStop()
        bind.mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        bind.mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    private fun observeSportsZoneHeatMap() {
        viewModel.sportsZonesHeatMap.observe(viewLifecycleOwner) {
            recyclerAdapter.data = it.legend!!
            it.geoRect!!.let { rectangle ->
                val latAmount = it.matrix!!.size
                val longAmount = it.matrix!![0].size

                val latOffset = rectangle.maxLat?.minus(rectangle.minLat!!)!! / latAmount
                val longOffset = rectangle.maxLng?.minus(rectangle.minLng!!)!! / longAmount

                var i = rectangle.minLat!!
                var iMatrix = 0
                var jMatrix: Int
                var j: Double
                var count: Int
                var color = 100
                while (i < rectangle.maxLat!!) {
                    count = 0
                    j = rectangle.minLng!!
                    jMatrix = 0
                    while (j < rectangle.maxLng!!) {
                        val rectPoints = ArrayList<Point>()
                        rectPoints.add(
                            Point(i, j)
                        )
                        Color.RED
                        rectPoints.add(
                            Point(i + latOffset, j)
                        )
                        rectPoints.add(
                            Point(i + latOffset, j + longOffset)
                        )
                        rectPoints.add(
                            Point(i, j + longOffset)
                        )
                        val rect: PolygonMapObject =
                            bind.mapview.map.mapObjects.addPolygon(
                                Polygon(LinearRing(rectPoints), ArrayList())
                            )
                        rect.strokeColor = Color.TRANSPARENT
                        val hexColor = java.lang.String.format(
                            "#%06X",
                            0xFFFFFF and it.matrix!![iMatrix % latAmount][jMatrix % longAmount]
                        )
                        rect.fillColor =
                            Color.parseColor("#66${hexColor.slice(1 until hexColor.length)}")
                        color += 1
                        j += longOffset
                        count++
                        jMatrix++
                    }
                    i += latOffset
                    iMatrix++
                }
            }
            bind.progressBar.visibility = View.GONE
            bind.loadingBackground.visibility = View.GONE
        }
    }

    private fun observeStatistics() {
        viewModel.statisctics.observe(viewLifecycleOwner) {
            if (it.sportzonesCount == null) bind.zonesText.visibility = View.GONE
            bind.zonesText.text = "Спортзон/100к: " + it.sportzonesCount.toString()
            if (it.square == null) bind.areaZones.visibility = View.GONE
            bind.areaZones.text = "Площадь спортзон/100к: " + it.square.toString()
            if (it.sportsCount == null) bind.amountText.visibility = View.GONE
            bind.amountText.text = "Кол-во видов спорт. услуг: " + it.sportsCount.toString()
        }
    }

    private fun observeHeatMap() {
        viewModel.heatMap.observe(viewLifecycleOwner) {
            recyclerAdapter.data = it.legend!!
            it.geoRect!!.let { rectangle ->
                val latAmount = it.matrix!!.size
                val longAmount = it.matrix!![0].size

                val latOffset = rectangle.maxLat?.minus(rectangle.minLat!!)!! / latAmount
                val longOffset = rectangle.maxLng?.minus(rectangle.minLng!!)!! / longAmount

                var i = rectangle.minLat!!
                var iMatrix = 0
                var jMatrix: Int
                var j: Double
                var count: Int
                var color = 100
                while (i < rectangle.maxLat!!) {
                    count = 0
                    j = rectangle.minLng!!
                    jMatrix = 0
                    while (j < rectangle.maxLng!!) {
                        val rectPoints = ArrayList<Point>()
                        rectPoints.add(
                            Point(i, j)
                        )
                        Color.RED
                        rectPoints.add(
                            Point(i + latOffset, j)
                        )
                        rectPoints.add(
                            Point(i + latOffset, j + longOffset)
                        )
                        rectPoints.add(
                            Point(i, j + longOffset)
                        )
                        val rect: PolygonMapObject =
                            bind.mapview.map.mapObjects.addPolygon(
                                Polygon(LinearRing(rectPoints), ArrayList())
                            )
                        rect.strokeColor = Color.TRANSPARENT
                        val hexColor = java.lang.String.format(
                            "#%06X",
                            0xFFFFFF and it.matrix!![iMatrix % latAmount][jMatrix % longAmount]
                        )
                        rect.fillColor =
                            Color.parseColor("#66${hexColor.slice(1 until hexColor.length)}")
                        color += 1
                        j += longOffset
                        count++
                        jMatrix++
                    }
                    i += latOffset
                    iMatrix++
                }
            }
            bind.progressBar.visibility = View.GONE
            bind.loadingBackground.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        recyclerAdapter = LegendAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext())

        bind.legendRecycler.run {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
        }
    }

    private fun observeFilters() {
        viewModel.filters.observe(
            viewLifecycleOwner
        ) {
            val removeAllChip = Chip(requireContext())
            removeAllChip.id = View.generateViewId()
            removeAllChip.text = "Сбросить"
            removeAllChip.chipBackgroundColor =
                ColorStateList.valueOf(Color.WHITE)
            removeAllChip.chipStrokeWidth = 10.dp.toFloat()
            removeAllChip.chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#2196F3"))
            removeAllChip.setTextColor(Color.parseColor("#2196F3"))
            removeAllChip.setOnClickListener {
                for (child in bind.filtersLayout.children) {
                    if (child is ChipGroup) child.clearCheck()
                }
                choosedFilters.clear()
            }
            removeAllChip.chipStrokeWidth = 2.dp.toFloat()
            //Добавление ChipGroup
            val removeGroup = ChipGroup(requireContext())
            removeGroup.id = View.generateViewId()

            bind.filtersLayout.addView(removeGroup)
            removeGroup.addView(removeAllChip)
            for (filterItem in it!!) {
                //Добавление ChipGroup
                val chipGroup = ChipGroup(requireContext())
                chipGroup.id = View.generateViewId()
                chipGroup.visibility = View.GONE

                //Добавление кнопки
                val button = MaterialButton(requireContext(), null, R.style.DropDownCustomButton)
                button.id = View.generateViewId()
                button.text = filterItem.title
                button.setTextColor(Color.BLACK)
                button.setOnClickListener {
                    chipGroup.visibility =
                        if (chipGroup.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
                button.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_bold)
                button.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.white);
                button.background =
                    requireContext().getDrawable(R.drawable.button_dropdown_background)
                button.setPadding(4.px, 5.px, 4.px, 5.px)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 4.px)
                button.layoutParams = params
                bind.filtersLayout.addView(button)
                bind.filtersLayout.addView(chipGroup)
                for (filter in filterItem.items!!) {
                    val chip = Chip(requireContext())
                    chip.id = View.generateViewId()
                    val drawable = createFromAttributes(
                        requireContext(),
                        null,
                        0,
                        R.style.CustomChipChoice
                    )
                    chip.setChipDrawable(drawable)
                    chip.chipBackgroundColor = ColorStateList.valueOf(Color.WHITE)
                    chip.chipStrokeWidth = 10.dp.toFloat()
                    chip.setTextColor(Color.parseColor("#2196F3"))
                    chip.chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#2196F3"))
                    chip.text = filter.name
                    chip.setOnClickListener { _chip ->
                        if (chip.isChecked) {
                            if (!choosedFilters.containsKey(filterItem.requestName))
                                choosedFilters[filterItem.requestName!!] =
                                    mutableListOf(filter.id!!)
                            else
                                choosedFilters[filterItem.requestName!!]!!.add(filter.id!!)
                        } else {
                            if (!choosedFilters.containsKey(filterItem.requestName))
                                choosedFilters[filterItem.requestName!!] =
                                    mutableListOf()
                            else
                                choosedFilters[filterItem.requestName!!]!!.remove(filter.id!!)
                        }
                        for (ids in choosedFilters.values) {
                            if (ids.contains(filter.id)) chip.isChecked = true
                        }
                    }
                    chipGroup.addView(chip)
                }
            }
        }
    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        bind.mapview.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
        return true
    }

    private val circleMapObjectTapListener =
        MapObjectTapListener { mapObject, point ->
            if (mapObject is CircleMapObject) {
                val userData = mapObject.userData
                if (userData is CircleMapObjectUserData) {
                    bind.bottomLayout.bottomSheet.visibility = View.VISIBLE
                    val mBottomSheetBehavior =
                        BottomSheetBehavior.from(bind.bottomLayout.bottomSheet)
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                    viewModel.getAttributes(userData.id)
                }
            }
            true
        }

    private class CircleMapObjectUserData internal constructor(
        val id: Int,
        val description: String
    )

    override fun onCameraPositionChanged(
        map: Map,
        position: CameraPosition,
        reason: CameraUpdateReason,
        boolean: Boolean
    ) {
        cameraPosition = position
    }
}