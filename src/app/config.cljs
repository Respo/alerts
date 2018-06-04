
(ns app.config )

(def dev?
  (if (exists? js/window) (do ^boolean js/goog.DEBUG) (= (-> js/process .-env .-env) "dev")))

(def site
  {:title "Alerts",
   :icon "http://cdn.tiye.me/logo/respo.png",
   :storage "Alerts",
   :dev-ui "http://localhost:8100/main.css",
   :release-ui "http://cdn.tiye.me/favored-fonts/main.css",
   :cdn-url "http://cdn.tiye.me/respo-alerts/",
   :cdn-folder "tiye.me:cdn/respo-alerts/",
   :upload-folder "tiye.me:repo/Respo/alerts/"})
