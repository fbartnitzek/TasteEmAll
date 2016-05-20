# list of improvements

## needed for NanoDegree
- Core Specs
    - App integrates a third-party library - DONE
    - App validates all input from servers and users. If data does not exist or is in the wrong format, the app logs this fact and does not crash. - DONE
    - App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio versions of audio cues. - DONE
    - App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts. - DONE
    - App provides a widget to provide relevant information to the user on the home screen. - DONE

- GooglePlayServices
    - App integrates two or more Google services - DONE (map & location)
    - Each service imported in the build.gradle is used in the app - DONE
    - If Location is used, the app customizes the user’s experience by using the device's location. - DONE
    - If Admob is used, the app displays test ads. If Admob was not used, student meets specifications.
    - If Analytics is used, the app creates only one analytics instance. If Analytics was not used, student meets specifications.
    - If Maps is used, the map provides relevant information to the user. If Maps was not used, student meets specifications. - DONE
    - If Identity is used, the user’s identity influences some portion of the app. If Identity was not used, student meets specifications.

- MaterialDesign
    - App theme extends AppCompat. - DONE
    - App uses an app bar and associated toolbars. - DONE
    - App uses standard and simple transitions between activities. - TODO?

- Building
    - App builds from a clean repository checkout with no additional configuration. - DONE?
    - App builds and deploys using the installRelease Gradle task. - TODO
    - App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.  - TODO
    - All app dependencies are managed by Gradle. - DONE

- Data Persistence
    - App implements a ContentProvider to access locally stored data.
    - Must implement at least one of the three
    - If it regularly pulls or sends data to/from a web service or API, app updates data in its cache at regular intervals using a SyncAdapter. OR
    - If it needs to pull or send data to/from a web service or API only once, or on a per request basis (such as a search application), app uses an IntentService to do so. OR
    - It it performs short duration, on-demand requests (such as search), app uses an AsyncTask. - DONE
    - App uses a Loader to move its data to its views. - DONE
    
- optional Stand Out:
    - Material Design (Shared element transitions, parallax scrolling) - shared element (lots more to to...) 
    - Notifications 
        - relevant
        - persistent if needed
        - multiple notifications might stack in one notification object
        - indicate context change like incoming message
        - expose information/controls to ongoing events like music playback
    - Sharing functionality (paragraph of text, link, description, image, ...)  - DONE
    - custom view if needed
    
## essential further improvements
- ShowProducer with drinks and reviews
- ShowDrinks with reviews
- date and time picker for review timestamp and filtering
- testCase for drinkType-validation (so it can be easily extended through configuration)
    - all drink / review attributes with optional specific drinkType-names (like producer_name_beer = brewer)
- update data model with 2 more entities
    - user 
        - create or autocomplete on reviews
        - better queries / easier to choose for non-default-user
    - location
        - clustering of multiple LatLng-Values around certain radius (f.e. 100m) get own locationEntry with locationDescription
        - needed for producers and reviews
        - map-queries relate directly to markers and can be represented in ListView with matching onClick-handling (intent to ShowLocation...)
        - attributes: centralLatLng, Radius (default one?), description, inputText, formattedText, Country
        - geocoding possible for producer-locations (currently no way to distinguish between Geocoded-text and raw-input-text)
        - locationPicker (multiple results possible for text-address) needed for producer-location
- complex filtering in main screen
    - all entries vs reviews only / ...
    - all users vs specific user
    - based on location of producer or review (f.e. country, ...)
    - based an date-range (vacation-mode)
    - words in review-description
    - combine all with AND (like Financisto)
    - ShowMap should than only Locations with complex join and where-clause
- location-handling with location-entities for producer/review
    a) Producer/Review-based:
        - query where location.desc="nameOfFriendsHome" and ...
        - List of Reviews with location.LatLng/ids => extract in activity / async task
    b) Location-based:
        - query all location with location-selection (f.e. certain country)
        - use locationId to query other entries for each locationEntry with other selection-args
        - location-query should be really fast for displaying markers on map
        - 2 or 3 loaders, all data prepared for displaying, way more queries if no limitation exists
    - some tests necessary (maybe based on selected filter different solution...)
- insert and update date for each entry (to show recently added entries)
- import / export 
    - other formats (xml / json / exploded csv)
    - more intelligent merging (might help for online-backend...)
    - optionally based on selection (like all reviews from vacation)
- delete functionality...? (cascading...?)
- improve logo with https://romannurik.github.io/AndroidAssetStudio/
- pictures for reviews and sharing
- showHelpFragment/Dialog
- icons for drinkTypes in MainFragment-lists, ...
- use GridLayout for Add / ShowEntries ...
    
## bugs
- wrong layout for widget (version-padding not working as expected)
- back-button sometimes needs to be pressed multiple times to get away from MainActivity/AddReview...? - still analyzing... - might be gone
- RTL support not working for drinkTypeSpinner in toolbar and ratingSpinner, just for drinkTypeSpinner in AddDrinks!

## online backend
- f.e. firebase as a start
- opt in
- producers and drinks should get uploaded "automatically" so less duplicate entries
- just for selected other users (maybe by email and confirmation) updates / sync on reviews
- review opt in for review details - mostly location and date (or less detailed, like month and city...)
- merging should be possible (maybe just admins...) with recursive renaming of id (not that ideal for rand-network-android-clients = eventual consistency ...)
- LATER :-p