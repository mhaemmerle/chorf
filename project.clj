(defproject chorf "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.2.0"]
                 [enlive "1.1.4"]
                 [compojure "1.1.5"]
                 [org.clojure/core.async "0.1.0-SNAPSHOT"]
                 [org.clojure/clojurescript "0.0-1889"]
                 [clj-time "0.6.0"]
                 [shodan "0.1.0"]
                 [org.clojure/google-closure-library-third-party "0.0-2029"]
                 [domina "1.0.1" :exclusions [org.clojure/clojurescript]]]
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :plugins [[lein-cljsbuild "0.3.3"]
            [lein-ring "0.8.3"]]
  :cljsbuild {:builds
              [{:source-paths ["src-cljs"],
                :compiler
                {:pretty-print true
                 :optimizations :simple
                 :output-to "resources/public/js/main.js"}}]}
  :jvm-opts ["-Djava.awt.headless=true"]
  :ring {:handler chorf.ring/app})
