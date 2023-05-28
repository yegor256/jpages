<img alt="logo" src="https://www.objectionary.com/cactus.svg" height="100px" />

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/yegor256/jpages/actions/workflows/mvn.yml/badge.svg)](https://github.com/yegor256/jpages/actions/workflows/mvn.yml)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/jpages/blob/master/LICENSE.txt)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/jpages)](https://hitsofcode.com/view/github/yegor256/jpages)

**jPages** is an experimental prototype of a web framework,
which respects all possible principles of
[true object-oriented](http://www.yegor256.com/2014/11/20/seven-virtues-of-good-object.html)
design. More about it in this
[blog post](https://www.yegor256.com/2019/03/26/jpages.html)
and in this [webinar](https://www.youtube.com/watch?v=bVzEPOZ_mDU).

This is how you start a web app:

```java
Thread thread = new Thread(
  () -> {
    App app = new App(
      new Page() {
        @Override
        public Page with(String name, String value) {
            if (value.equals("/")) {
              return new TextPage("Hello, world!");
            }
            return new TextPage("Not found!");
        }
        @Override
        public Output via(Output output) {
          return output.with("X-Body", "Not found");
        }
      }
    );
    try {
      app.start(8080);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      throw new IllegalStateException(ex);
    }
  }
);
thread.setDaemon(true);
thread.start();
```

This repository is read-only now. Check [yegor256/takes](https://github.com/yegor256/takes) instead, it's in active development.
