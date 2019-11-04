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
          "div.breadcrumb span a span"
          (soup/text)))
