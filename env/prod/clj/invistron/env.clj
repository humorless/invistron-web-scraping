(ns invistron.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[invistron started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[invistron has shut down successfully]=-"))
   :middleware identity})
