(ns invistron.scrape.page
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [jsoup.soup :as soup]
   [invistron.config :refer [env]]
   [clojure.tools.logging :as log]
   [yaml.core :as yaml]))

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
  (first
   (soup/$ (soup/get! url)
           "ul.zoom_thumbnails > li > a"
           (soup/attr "abs:href"))))

(defn ->filename [url]
  (string/replace
   (string/replace url #"https://invistron.en.taiwantrade.com/product/" "")
   #"html"
   "md"))

(defn gen-md-file [url]
  (let [f (->filename url)
        b (parse-breadcrumb (get-breadcrumb url))
        m (parse-model (get-model url))
        data (merge b m)
        y-data (yaml/generate-string data :dumper-options {:flow-style :block})
        content (str "---\n" y-data "---\n")]
    (spit (str "md/" f) content)))
