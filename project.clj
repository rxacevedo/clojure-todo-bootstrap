(defproject todo "0.1.0-SNAPSHOT"
  :description "Clojure webdev tutorial project - a simple Postgres-backed todo list."
  :url "https://github.com/rxacevedo/clojure-todo-bootstrap"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.0"]
                 [compojure "1.3.1"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [postgresql/postgresql "9.1-901.jdbc4"]]
  :min-lein-version "2.0.0"
  :uberjar-name "todo.jar"
  :main todo.core
  :profiles {:uberjar {:aot :all}})
