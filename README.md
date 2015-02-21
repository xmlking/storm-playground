Storm Playground
=========

### Getting Started

```bash
# Install GVM
curl -s get.gvmtool.net | bash
# Install gradle with GVM
gvm install gradle

```

### Open project with Intellij IDEA

`File` -> `Open...` and browse to `build.gradle` file.

### Running Examples
```bash 
gradle example1
gradle example2
gradle example3
gradle example4
```

#### Example #5
You need to set two environment variables to use the Twitter Streaming API:

```bash 
$ export TWITTER_USERNAME="your username"
$ export TWITTER_PASSWORD="your password"
# or set twitter credentials in build.gradle 
$ gradle example5 -Pargs='--name local --filter #xmlking'
```

You will need to create a simple Twitter app [here](https://dev.twitter.com/) and insert your own auth values in `twitter4j.properties`. 
This is necessary to consume the public stream or perform any other twitter-specific operations.

### Ref
https://github.com/yrro/stormygroove/tree/master/src/main/groovy
