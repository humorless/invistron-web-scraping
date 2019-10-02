(ns invistron.scrape.core
  (:require
   [clojure.string :as string]
   [etaoin.api :as w]
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

#_(soup/$ (soup/slurp! "/vagrant/index.html")
          "div.product-list div.product-name a[href]"  ;; Jsoup selector
          (soup/attr "abs:href"))

#_(defn get-product-urls [url]
    (soup/$ (soup/get! url)
            "div.product-list div.product-name a[href]"  ;; Jsoup selector
            (soup/attr "abs:href")))                     ;; attribute selector

(def driver (w/chrome-headless))

(defn scrape-product-urls-by-browser [url]
  (log/info "begin to scrape " url)
  (w/go driver url)
  (w/wait-visible driver {:fn/has-classes [:template :product-list]})
  (map #(w/get-element-attr-el driver % "href")
       (w/query-all driver {:css "div.product-list div.product-name a"})))

(defn scrape-and-spit [url]
  (prn "running ...")
  (doseq [product-url (scrape-product-urls-by-browser url)]
    (spit "url.edn" (prn-str product-url) :append true)))

(defn process []
  (let [p-urls (all-page-urls)]
    (map scrape-and-spit p-urls)))
