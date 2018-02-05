(ns character-sheet.endpoint.static
  (:require [compojure.core :refer :all]
            [ring.util.response :as resp]
            [sweet-tooth.endpoint.liberator :as el]
            [integrant.core :as ig]))

(defn endpoint
  [component]
  (routes (GET "/main.js" [] (resp/resource-response "main.js"))
          ;; load the single page app
          (GET "/" [] (el/html-resource "index.html"))
          (GET "/character-sheet/*" [] (el/html-resource "index.html"))))


(defmethod ig/init-key :character-sheet.endpoint/static [_ options]
  (endpoint nil))
