# Clarc

Code repo for my
[presentation](https://github.com/fdserr/clarc/blob/master/resources/public/deck.pdf):
## ClojureScript: Back To Front.

Devcards latest build is at: [http://clarc.s3-website-ap-southeast-1.amazonaws.com/index.html](http://clarc.s3-website-ap-southeast-1.amazonaws.com/index.html)

App latest build is at: [http://clarc.s3-website-ap-southeast-1.amazonaws.com/app/index.html](http://clarc.s3-website-ap-southeast-1.amazonaws.com/app/index.html)

## Hacking

[![CircleCI](https://circleci.com/gh/fdserr/clarc.svg?style=svg&circle-token=2ea7757a3e32217c21186f8b4469ebe39163245b)](https://circleci.com/gh/fdserr/clarc)

### Pre-requisites

- [JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
- [Leiningen](https://leiningen.org)
- [Chrome](https://www.google.com/chrome/) or [Canary](https://www.google.com/chrome/browser/canary.html)
- [rlwrap](http://brewformulas.org/Rlwrap) if you're on OSX

### Nice to have

- [Git](https://desktop.github.com)
- [PhantomJS](http://phantomjs.org/download.html)
- [AWS CLI](https://aws.amazon.com/cli/)
- [CircleCI CLI](https://circleci.com/docs/2.0/local-jobs/)

### Getting started

From the main project dir, run in a terminal:
`lein figwheel` or `rlwrap lein figwheel` on OSX.
When the dust settles down, browse to
`http://localhost:3450/index.html`.

### Contact

@fdserr on Twitter, GitHub, and Medium.
