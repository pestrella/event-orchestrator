;; Author: Paolo Estrella
(ns noble.orchestrator
  (import [com.thisisnoble.javatest Orchestrator]
          [com.thisisnoble.javatest.impl CompositeEvent]))

(def processors (atom []))
(def publisher (atom nil))
(def events (atom {}))

(defn get-root [events event]
  (loop [parent (events (.getParentId event))
         event nil]
    (if parent
      (recur (.getParent parent) parent)
      event)))

(defn collate [events event]
  (let [parent (get-root events event)]
    (assoc (if parent
             (assoc events
               (.getId parent)
               (doto parent
                 (.addChild event)))
             events)
      (.getId event)
      (CompositeEvent. event parent))))

(defn process [processor event]
  (when (.interestedIn processor event)
    (.process processor event)))

(def orchestrator
  (reify Orchestrator
    (setup [this pub]
      (reset! publisher pub))
    (register [this processor]
      (swap! processors conj processor))
    (receive [this event]
      (do
        (swap! events collate event)
        (.publish @publisher (get-root @events event))
        (let [agents (doall (map #(agent %) @processors))]
          (doseq [agent agents]
            (send-off agent process event)))))))
