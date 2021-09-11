# Vaccination-tracker

## See more:
https://github.com/iurysza/vacctracker-pod-repo
https://github.com/iurysza/multiplaform-client
https://github.com/iurysza/vactracker-android-client

A library that provides Brazilian vaccination data targeting iOS, Android and the Jvm.

## Android

There's always a `release` and `debug` version of the same task, so replace `$variant` for the one you need.

### Building

Creating debug and release `aar`:

```bash
$ gradle :vaccination-tracker:bundle${variant}Aar
```

These will create an `.aar` in the `vaccination-tracker/build/libs/` directory, that can be imported in any android project directly.

### Publishing the library

To publish the library to your local maven repository you just need to run:

```bash
$ gradle :vaccination-tracker:publish${variant}PublicationToMavenLocal
```

OR

You can publish it to `mavenCentral` . To do that you'll need to sign it. Fill this data in your `local.properties`.

```groovy
ossrhUsername=username
ossrhPassword=password
sonatypeStagingProfileId=stagingId
signing.key=key
signing.keyId=keyId
signing.password=keyPassword
```

Then, you can run:

```bash
$ gradle :vaccination-tracker:publish${variant}PublicationToSonatypeRepository
```

Either way, you can import it by adding

```kotlin
dependencies {
	implementation("com.github.iurysza:vaccination-tracker:$version")
}
```

## iOS

### Building

For the iOS we'll use `UniversalFatFramework` to be able to share this library with other developers.
These are `.framework`s with extra benefits:

- **Universal** means they run on any iOS architecture (simulators and devices)
- **Fat** means that they're self contained. A dependencies are there.

There's also a `release` and `debug` version of the same task, so replace `$variant` for the one you need.

```bash
$ gradle :vaccination-tracker:universal${variant}Framework
```

### Publishing
--- WIP ---
