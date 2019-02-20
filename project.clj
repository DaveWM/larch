(defproject larch "0.1.1"
  :description "An elm-like clojurescript framework"
  :url "An elm-like clojurescript framework"
  :license {:name "GPL V3"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-doo "0.1.10"]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.async "0.4.490"]]
  :cljsbuild {
              :builds [{:id "prod"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "dist/main.min.js"
                                   :optimizations :advanced
                                   :pretty-print true}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "main/testable.js"
                                   :main runner
                                   :optimizations :none}}]})
