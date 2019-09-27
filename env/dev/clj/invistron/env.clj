(ns invistron.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [invistron.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[invistron started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[invistron has shut down successfully]=-"))
   :middleware wrap-dev})
