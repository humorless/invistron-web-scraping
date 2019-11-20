(ns invistron.scrape.page
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [jsoup.soup :as soup]
   [invistron.config :refer [env]]
   [clojure.tools.logging :as log]
   [yaml.core :as yaml]
   [clj-http.client :as client]))

(defn read-urls []
  (with-open [rdr (io/reader "resources/url.edn")]
    (doall (map read-string (line-seq rdr)))))

(defn get-breadcrumb [url]
  (soup/$ (soup/get! url :timeout 0)
          "div.breadcrumb > span > a > span"
          (soup/text)))

(defn parse-breadcrumb [s-tuple]
  {:type   (nth s-tuple 2)
   :vendor (nth s-tuple 3)
   :title  (nth s-tuple 4)})

(defn get-model [url]
  (soup/$ (soup/get! url :timeout 0)
          "div.content > div > div > dl > dd"
          (soup/text)))

(defn parse-model [s-tuple]
  {:model (first s-tuple)})

(defn get-table-left-columns [url]
  (soup/$ (soup/get! url :timeout 0)
          "#productDetails > div:nth-child(2) > div > table > tbody > tr > td:nth-child(1)"
          (soup/text)))

(defn parse-left-columns [s-tuple]
  {:left-cols s-tuple})

(defn get-table-right-columns [url]
  (soup/$ (soup/get! url :timeout 0)
          "#productDetails > div:nth-child(2) > div > table > tbody > tr > td:nth-child(2)"
          (soup/text)))

(defn parse-right-columns [s-tuple]
  {:right-cols s-tuple})

(defn get-img-url [url]
  (first
   (soup/$ (soup/get! url :timeout 0)
           "ul.zoom_thumbnails > li > a"
           (soup/attr "abs:href"))))

(defn ->filename [url]
  (string/replace
   (string/replace url #"https://invistron.en.taiwantrade.com/product/" "")
   #"html"
   "md"))

(defn download-image [url]
  (prn "download image from " url)
  (let [img-url (get-img-url url)
        filename (string/replace img-url #"http.*/" "")]
    (clojure.java.io/copy
     (:body (client/get img-url {:as :stream}))
     (java.io.File. (str "image/" filename)))))

(defn gen-md-file [url]
  (prn "generate md file from " url)
  (let [f (->filename url)
        b (parse-breadcrumb (get-breadcrumb url))
        m (parse-model (get-model url))
        img-filename (string/replace (get-img-url url) #"http.*/" "")
        l (parse-left-columns (get-table-left-columns url))
        r (parse-right-columns (get-table-right-columns url))
        data (merge b m l r {:img img-filename})
        y-data (yaml/generate-string data :dumper-options {:flow-style :block})
        content (str "---\n" y-data "---\n")]
    (spit (str "md/" f) content)))

(defn process [f]
  (let [urls (read-urls)]
    (pmap f urls)))

;; (process gen-md-file)
;; (process download-image)
