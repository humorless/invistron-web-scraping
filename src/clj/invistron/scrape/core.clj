(ns invistron.scrape.core
  (:require
   [clojure.string :as string]
   [clj-http.client :as client]
   [jsoup.soup :as soup]
   [mount.core :refer [defstate]]
   [invistron.config :refer [env]]
   [clojure.tools.logging :as log]))

(defstate ^:dynamic *url*
  :start (:product-url env)
  :stop "")

(defn n->page-url [url-template n]
  (string/replace url-template #"PAGENUM" (str n)))

(defn all-page-urls []
  (let [page-num (:page-num env)
        numbers (range 1 (inc page-num))]
    (map #(n->page-url *url* %) numbers)))

(defn page-url->product-urls [url])
