(ns invistron.scrape.core
  (:require
   [clojure.string :as string]
   [etaoin.api :as etaoin]
   [etaoin.keys :as k]
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

(soup/$ (soup/slurp! "/vagrant/index.html")
        "div.product-list div.product-name a[href]"  ;; Jsoup selector
        (soup/attr "abs:href"))

(defn get-product-urls [url]
  (soup/$ (soup/get! url)
          "div.product-list div.product-name a[href]"  ;; Jsoup selector
          (soup/attr "abs:href")))                     ;; attribute selector

(defn all-product-urls []
  (map get-product-urls (all-page-urls)))
