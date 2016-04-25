witchhunt
============================

***EXPERIMENTAL*** tool to find unused/redundant CSS rules.

### Operation

- Tell _witchhunt_ where to find your site's styleguide
- _witchhunt_ will find site-local CSS files referenced from this
- The styleguide page and any site-local pages linked from it will then be checked against all the site-local CSS rules
  - Any local rules that are not matched will be reported
  - Any local rules that are redundant will be reported



### Still To-Do

All of it.

### Credits

[PH-CSS](https://github.com/phax/ph-css): Parsing CSS stylesheets

[Scoup](https://github.com/themillhousegroup/scoup) (and hence [JSoup](https://jsoup.org/)): Selecting elements in HTML files

