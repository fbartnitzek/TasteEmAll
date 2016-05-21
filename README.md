# TasteEmAll
Review and rate every new drink you try - and just try the good ones again.

Too many apps to chose where to note your favorite beers, but none for the other drinks you like?
Lots of ratings in those apps from guys who seem to just like variations of water?

So you tried some and decided to use lists instead?
And in the end you’re tired of using multiple lists with hundreds of entries, one for each kind of drink?

Wouldn’t it be great to know when and where you tried them without typing and searching and typing? 
And publish your review to the public if - and only if - you like?

Here is a solution which hopefully will at least work for me. :-)


## Installation
To include google maps for the map and the geocoder, the API Key (or a reference to it) needs to be inserted below `app/src/debug/res/values` and `app/src/release/res/values` in a file f.e. **api_keys.xml** with the following format:
```
<resources>
    <string name="GoogleMapsApiKey">INSERT_KEY_HERE</string>
</resources>
```

The signing configuration for the release build uses 4 gradle-properties, which are configures in `~/.gradle/gradle.properties`:
```
RELEASE_STORE_FILE=/home/userName/keystores/android.jks
RELEASE_STORE_PASSWORD=*******
RELEASE_KEY_ALIAS=MYANDROIDKEY
RELEASE_KEY_PASSWORD=********
```