(ns character-sheet.duct.middleware
  (:require [integrant.core :as ig]
            [ring.middleware.format :as f]
            [character-sheet.middleware.flush :as flush]
            [character-sheet.middleware.body-params :as body-params]))

(defmethod ig/init-key ::transit-json [_ options]
  #(f/wrap-restful-format % options))

(defmethod ig/init-key ::flush [_ _]
  #(flush/wrap-flush %))

(defmethod ig/init-key ::body-params [_ _]
  #(body-params/wrap-body-params %))
