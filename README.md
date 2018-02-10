# Clarc

Devcards latest build is at: [https://http://clarc.s3-website-ap-southeast-1.amazonaws.com/cards.html](http://clarc.s3-website-ap-southeast-1.amazonaws.com/cards.html)

## Hacking

### Pre-requisites

- [JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
- [Leiningen](https://leiningen.org)
- [Chrome](https://www.google.com/chrome/) or [Canary](https://www.google.com/chrome/browser/canary.html)
- [rlwrap](http://brewformulas.org/Rlwrap) if you're on OSX
- [Git](https://desktop.github.com)
- [PhantomJS](http://phantomjs.org/download.html)

### Getting started

From the main project dir, run in a terminal:
`lein figwheel` or `rlwrap lein figwheel` on OSX.
When the dust settles down, browse to
`http://localhost:3449/cards.html#!/elvn.index` and read on.

### Troubleshooting
