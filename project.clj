(defproject webdev "0.1.0-SNAPSHOT"
  :description "Clojure webdev tutorial project"
  :url "http://cljthing.herokuapp.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.2"]
                 [compojure "1.1.7"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [postgresql/postgresql "9.1-901.jdbc4"]]
  :min-lein-version "2.0.0"
  :uberjar-name "webdev.jar"
  :repl-options {:init-ns user}
  :main webdev.core
  :profiles {:uberjar {:aot :all}})
