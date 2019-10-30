[![](https://jitpack.io/v/twak/jutils.svg)](https://jitpack.io/#twak/jutils)

twak's java utils!

Random tools and abstractions created as required. Not a tidy library! Required for some of my other projects. Not all my own work. 

## integration

### maven
Add this to your `pom.xml` to use latest snapshot in your project:

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.twak</groupId>
    <artifactId>jutils</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

### local maven
`mvn compile install` should install it into your local mvn.

### gradle
Add this in your root `build.gradle` at the end of repositories to use latest snapshot:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
        implementation 'com.github.twak:jutils:master-SNAPSHOT'
}
```
