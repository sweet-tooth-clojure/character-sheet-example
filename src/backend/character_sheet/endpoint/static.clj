(ns character-sheet.endpoint.static
  (:require [compojure.core :refer :all]
            [ring.util.response :as resp]
            [sweet-tooth.endpoint.utils :as c]))

(defn endpoint
  [component]
  (routes (GET "/main.js" [] (resp/resource-response "main.js"))
          ;; load the single page app
          (GET "/" [] (c/html-resource "index.html"))
          (GET "/character-sheet/*" [] (c/html-resource "index.html"))))
