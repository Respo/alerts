
(ns respo-alerts.main
  (:require [respo.core :refer [render! clear-cache! realize-ssr!]]
            [respo-alerts.comp.container :refer [comp-container]]
            [respo-alerts.updater :refer [updater]]
            [respo-alerts.schema :as schema]
            [reel.util :refer [listen-devtools!]]
            [reel.core :refer [reel-updater refresh-reel]]
            [reel.schema :as reel-schema]
            [cljs.reader :refer [read-string]]
            [respo-alerts.config :as config]))

(defonce *reel
  (atom (-> reel-schema/reel (assoc :base schema/store) (assoc :store schema/store))))

(defn dispatch! [op op-data]
  (when config/dev? (println "Dispatch:" op op-data))
  (reset! *reel (reel-updater updater @*reel op op-data)))

(def mount-target (.querySelector js/document ".app"))

(defn render-app! [renderer]
  (renderer mount-target (comp-container @*reel) #(dispatch! %1 %2)))

(def ssr? (some? (js/document.querySelector "meta.respo-ssr")))

(defn main! []
  (if ssr? (render-app! realize-ssr!))
  (render-app! render!)
  (add-watch *reel :changes (fn [] (render-app! render!)))
  (listen-devtools! "a" dispatch!)
  (.addEventListener
   js/window
   "beforeunload"
   (fn [] (.setItem js/localStorage (:storage config/site) (pr-str (:store @*reel)))))
  (let [raw (.getItem js/localStorage (:storage config/site))]
    (if (some? raw) (do (dispatch! :hydrate-storage (read-string raw)))))
  (println "App started."))

(defn reload! []
  (clear-cache!)
  (reset! *reel (refresh-reel @*reel schema/store updater))
  (println "Code updated."))
