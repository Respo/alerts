
(ns app.config )

(def dev? (do ^boolean js/goog.DEBUG))

(def site
  {:title "Alerts",
   :icon "http://cdn.tiye.me/logo/respo.png",
   :storage "Alerts",
   :dev-ui "http://localhost:8100/main.css",
   :release-ui "http://cdn.tiye.me/favored-fonts/main.css",
   :cdn-url "http://cdn.tiye.me/respo-alerts/",
   :cdn-folder "tiye.me:cdn/respo-alerts/",
   :upload-folder "tiye.me:repo/Respo/alerts/"})
