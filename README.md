

Swapp, a stupid simple web application with client/server GET/POST
--------------

This is a simple project created with `lein new simple-web-app' but then with
reagent-forms, cljs-ajax and midje added.


Running it.
-----------

```lein figwheel```

and 

```lein run```  

Should get everything running.

What is this?
-------------
 
 This is essentially a test project to learn reagent and how to make a 
 web application as simply as possible. 
 
 Most of the cljs code is from reagent-forms-example.  Although Scramble?
 has been added and there is a round trip GET to the server for
 determining if the word can be spelled from the letters in the scramble.

 It would be nice to have the form be more interactive. As it is, the document
 atom is displayed at the bottom of the page.
 
 My plan is to refactor this with reframe for a better application experience.
 
 Scramble? is case sensitive, and works just fine for symbols and numbers as well.
 See the test case for examples.
 
 

 
