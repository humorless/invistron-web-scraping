(ns invistron.scrape.page
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [jsoup.soup :as soup]
   [invistron.config :refer [env]]
   [clojure.tools.logging :as log]))

(defn read-urls []
  (with-open [rdr (io/reader "resources/url.edn")]
    (doall (map read-string (line-seq rdr)))))

(defn get-breadcrumb [url]
  (soup/$ (soup/get! url)
          "div.breadcrumb > span > a > span"
          (soup/text)))

(defn parse-breadcrumb [s-tuple]
  {:type   (nth s-tuple 2)
   :vendor (nth s-tuple 3)
   :title  (nth s-tuple 4)})

(defn get-model [url]
  (soup/$ (soup/get! url)
          "div.content > div > div > dl > dd"
          (soup/text)))

(defn parse-model [s-tuple]
  {:model (first s-tuple)})

(defn get-img-url [url]
  (soup/$ (soup/get! url)
          "ul.zoom_thumbnails > li > a"
          (soup/attr "abs:href")))

(defn ->filename [url]
  (string/replace
   (string/replace url #"https://invistron.en.taiwantrade.com/product/" "")
   #"html"
   "md"))
