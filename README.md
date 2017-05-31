# larch (Alpha)

![The Larch](https://s-media-cache-ak0.pinimg.com/originals/17/ff/7f/17ff7f207250309896e0d1f859c9ed41.jpg)

Larch is a very minimal framework, loosely following the Elm architecture. The main goal of Larch is to provide an easy-to-use, simple, and scaleable framework for web apps.

## Overview

The only function Larch provides is `actions->transactions!`. This takes the following arguments:

* `event-chan` - a channel which outputs all the events that your app cares about. These events may be triggered by user interaction, or other global events (loss of internet connection, time changes, etc.). These events can be in any format whatsoever (e.g. strings, keywords, tuples).
* `app-db` - an atom containing the whole state of your app. It is recommended to use [datascript](https://github.com/tonsky/datascript) for this, but it can be a map if you prefer.
* `dependencies` - a map containing any impure functions and references to mutable objects that your app needs. For example, if your app needs to make http requests, and also update local storage, your dependencies may look like: `{:fetch js/fetch :local-storage js/localStorage}`.
* `process-event` - a function which takes an event and the app db value (i.e. the value of the `app-db` atom), and returns a tuple of 1 or 2 elements (the second element is optional):
  1. An update to the app db, either expressed as data or a pure function. This update is put onto the returned "transaction" channel (more on that later).
  2. A function, henceforth refered to as the "command channel", that takes the app db value and the dependencies map, and returns a channel of events. All impure or asynchronous operations should be performed in this function.
  
The `actions->transactions!` function returns a channel of "transactions". In order to avoid making assumptions about the format of your app db, Larch does not actually update your app db itself. You must listen to the transactions channel and update the app db.

## Transactions

A transaction represents an update to the app db. If you're using datascript for the app db, these transactions will just be datascript transactions. If you're using a map (or something else), then you have 2 options:

* Transactions can be pure functions that take the current app db, and return the new one.
* Transactions can be data, which you then use to update the app db.

## Usage

Add `[davewm/larch 0.0.1]` as a dependency.

For a reference example, please see [monzo-cljs](https://github.com/DaveWM/monzo-cljs).

## License

Distributed under the GPL V3 license.
