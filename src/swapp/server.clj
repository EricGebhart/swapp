(ns swapp.server
  (:gen-class)
  (:require
   [swapp.scramble :refer :all]
   [compojure.core :refer [routes GET POST]]
   [compojure.route :as route]
   [environ.core :refer [env]]
   [hiccup.element :refer [javascript-tag]]
   [hiccup.page :refer [html5 include-css include-js]]
   [immutant.web :as immutant]
   [ring.middleware.json :as middleware]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.webjars :refer [wrap-webjars]]
   [selmer.parser :as parser]
   [ring.util.response :refer [response]]))

(defn resource [r]
  (-> (Thread/currentThread)
      (.getContextClassLoader)
      (.getResource r)
      slurp))

(defn render-home-page
  []
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE-edge"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:title "swapp :: Home Page"]
    (include-css "/assets/bootstrap/css/bootstrap.min.css")
    (include-css "/assets/bootstrap/css/bootstrap-theme.min.css")
    (include-css "css/screen.css")
    (include-css "css/app.css")
    (include-js "//cdnjs.cloudflare.com/ajax/libs/react/0.11.0/react.js")
    (include-js "cljs/app.js")]
   [:body
    [:div#app.container [:h1 "Waiting for ClojureScript to load ..."]]
    (javascript-tag "swapp.client.run();")]))

(def app-routes
  (routes
   (GET "/hello" [] "Hello World!")
   (GET "/test" [] {:hello "world"})
   (GET "/home" [] (render-home-page))
   (GET "/" []
        (parser/render-file "templates/app.html"
                            {:forms-css (resource "reagent-forms.css")
                             :json-css (resource "json.human.css")}))
   (GET "/scramble/:tst-str/:src-str" [tst-str src-str] (scramble tst-str src-str))
   (GET "/scrambled" {s :params}
        (response (scramble? (s :src-str) (s :word))))
   (GET "/s" {s :params}
        (response
         {:found
          (scramble? (s :src-str) (s :word))}))

   (route/resources "/")
   (route/not-found "not found")))

(def handler
  (as-> app-routes h
    (if (:dev? env) (wrap-reload h) h)
    (wrap-defaults h (assoc-in site-defaults [:security :anti-forgery] false))
    (wrap-webjars h)
    #_(wrap-params h)
    (wrap-keyword-params h)
    (middleware/wrap-json-params h)
    (middleware/wrap-json-response h)
    ))


#_(def handler
    (-> app-routes
        wrap-webjars
        wrap-keyword-params
        wrap-params
        wrap-multipart-params
        middleware/wrap-json-params
        middleware/wrap-json-response
        ))

;;; from another example
(def app
  (-> app-routes
      wrap-keyword-params
      middleware/wrap-json-params
      middleware/wrap-json-response))

(defn run-server
  []
  (immutant/run handler {:port 8080}))

(defn -main
  [& args]
  (run-server))
