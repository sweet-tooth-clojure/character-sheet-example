(ns character-sheet.system
  (:require [duct.util.system :as duct-system]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [meta-merge.core :refer [meta-merge]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.format :as f]))

(def base-config
  '{:app {:middleware
          {:functions
           {:log-errors     ring.middleware.stacktrace/wrap-stacktrace-log
            :not-found      duct.middleware.not-found/wrap-not-found
            :ring-defaults  ring.middleware.defaults/wrap-defaults
            :route-aliases  duct.middleware.route-aliases/wrap-route-aliases
            :transit-json   ring.middleware.format/wrap-restful-format
            :flush          character-sheet.middleware.flush/wrap-flush
            :body-params    character-sheet.middleware.body-params/wrap-body-params}
           :applied [:not-found
                     :body-params
                     :ring-defaults
                     :transit-json
                     :route-aliases
                     :log-errors
                     :flush]
           :arguments
           {:not-found     "not found"
            ;;:hide-errors   "hide errors"
            :route-aliases {"/" "/index.html"}
            :log-errors    {:color? true}
            :ring-defaults {:params    {:urlencoded true
                                        :keywordize true
                                        :multipart  true
                                        :nested     true}
                            :cookies   true
                            :session   {:flash true
                                        :cookie-attrs {:http-only true}}
                            :security  {:anti-forgery   false
                                        :xss-protection {:enable? true, :mode :block}
                                        :frame-options  :sameorigin
                                        :content-type-options :nosniff}
                            :static    {:resources ""}
                            :responses {:not-modified-responses false
                                        :absolute-redirects     true
                                        :content-types          true
                                        :default-charset        "utf-8"}}
            :transit-json {:formats [:transit-json]}}}}})


(defn endpoint-deps
  [system]
  (let [endpoints (keys (:endpoints system))]
    (-> system
        (assoc-in [:dependencies :app] (vec endpoints))
        (update :dependencies merge (reduce (fn [xs x] (assoc xs x [:db]))
                                            {}
                                            endpoints)))))

(defn system-desc
  [config]
  (endpoint-deps
    `{:components {:app  duct.component.handler/handler-component
                   :http ring.component.jetty/jetty-server
                   :db   system.components.datomic/new-datomic-db}
      :endpoints {:character-sheet character-sheet.endpoint.character-sheet/endpoint
                  :static character-sheet.endpoint.static/endpoint}
      :dependencies {:http [:app]}
      :config ~(meta-merge base-config config)}))

(defn new-system
  [config]
  (-> config
      system-desc
      duct-system/build-system))
