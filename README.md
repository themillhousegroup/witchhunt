	witchhunt
============================

***EXPERIMENTAL*** tool to find unused/redundant CSS rules.

### Rationale
Having a live styleguide offers great benefits to any dynamic website. But sometimes it can be hard to know if your styleguide is truly comprehensive. Does it actually exercise every bit of styling specified in your local CSS files? Or from the opposite perspective, do you have CSS rules that are no longer needed?


See also - [CSS Purge](http://www.csspurge.com/) which has provided inspiration for lots of these warnings/violations.

### Operation

- Tell _witchhunt_ where to find your site's styleguide
- _witchhunt_ will find site-local CSS files referenced from this
- The styleguide page and any site-local pages linked from it will then be checked against all the site-local CSS rules
  - Any local rules that are not matched will be reported
  - Any local rules that are redundant will be reported

### Building the executable
Preconditions: you have a working SBT stack (i.e. Java and SBT installed)

```
% sbt stage
```

### Running the executable

On UNIX-like systems:

```
% ./target/universal/stage/bin/witchhunt [options] <target-url>
```

On Windows systems:

```
% ./target/universal/stage/bin/witchhunt.bat [options] <target-url>
```

#### Options
Invoke `witchhunt` with no arguments to get the up-to-date list.

### Still To-Do
- Detect and parse `.map` files and use them to give correct locations in LESS and SASS pre-processor files
- Warn about excessive nesting a.k.a [The Inception Rule](http://www.csspurge.com/) (3 levels down)
- Warn about [excessive colors](http://www.csspurge.com/)
- Warn about [excessive media querying](http://www.csspurge.com/)
- Warn about [excessive numbers of style links](http://www.csspurge.com/)
- Warn about [excessive numbers of font sizes](http://www.csspurge.com/)
- Warn about [excessive numbers of font families](http://www.csspurge.com/)
- Warn about [excessive specificity](http://www.csspurge.com/)




### Credits

[PH-CSS](https://github.com/phax/ph-css): Parsing CSS stylesheets

[Scoup](https://github.com/themillhousegroup/scoup) (and hence [JSoup](https://jsoup.org/)): Selecting elements in HTML files

