(ns chorf.ring
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(defn wrap-dir-index
  [handler]
  (fn [req]
    (handler
     (update-in req [:uri] #(if (= "/" %) "/index.html" %)))))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site app-routes)
      (wrap-dir-index)))
