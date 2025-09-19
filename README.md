# TastiCalendar

A simple library, based on Material You, to implement a monthly or yearly calendar containing a collection of dates

<p align='center'>
  <a href='https://github.com/m-i-n-a-r/birday/blob/master/LICENSE.md'><img src='https://img.shields.io/badge/license-GPL 3-333333'/></a>
  <a href="https://jitpack.io/#m-i-n-a-r/tasticalendar"><img src="https://jitpack.io/v/m-i-n-a-r/tasticalendar.svg" /></a>
  <a href="https://m-i-n-a-r.github.io/tasticalendar/">
  <img alt="Javadoc" src="https://img.shields.io/badge/Read%20the%20documentation-7F52FF?logo=readthedocs&logoColor=white" />
  </a>
</p>

## Introduction
I wrote this library starting from a piece of [Birday](https://www.github.com/m-i-n-a-r/birday), since I noticed that there isn't a similar library (at least, not a recent one). I kept it super simple and light, but I'm open to any pull request. 
**Important**: this library is provided as is, no updates are guaranteed since I have other projects to focus on. It works (I use it personally in 2 projects) and is quite complete in my opinion, but I'm open to any criticism. This library doesn't need any translation since it doesn't use any strings itself.

## How to use
1. Open the ```build.gradle (Project level)``` and, under repositories, make sure to have:\
```maven { url "https://jitpack.io" }```

2. Open the ```build.gradle (Module:app)``` file of your app, and under dependencies, add:\
``` implementation 'com.github.m-i-n-a-r:tasticalendar:1.4.0' ```

3. Sync Gradle, and you're good to go!

4. A simple example of use can be found in my Birday app, in [this file](https://github.com/m-i-n-a-r/birday/blob/master/app/src/main/java/com/minar/birday/fragments/OverviewFragment.kt) and in the corresponding [layout](https://github.com/m-i-n-a-r/birday/blob/master/app/src/main/res/layout/fragment_overview.xml). Or, you can [read the docs!](https://m-i-n-a-r.github.io/tasticalendar/)

## Features
<p align='center'>
  <img src='https://i.imgur.com/3k8buMR.jpg' width='90%'/><br>
  Monet support examples<br><br><br>
  <img src='https://i.imgur.com/ENyIwMJ.jpg' width='90%'/><br>
  3 of the four available scale factors<br><br><br>
</p>

- Super lightweight
- The color scheme automatically adapts to the app (Material Expressive)
- Month layout (with different scales)
- Year layout (adaptive depending on the month scale factor)
- Clickable days, clickable month titles
- Disable weekdays, different sunday highlight strategies
- Automatic or manual "Sunday as first day of the week" 
- Easily set any property or render a different year/month
- Easily pass a collection of dates or TastiCalendarEvent objects to highlight a set of dates on a month
- The library chooses the best contrast for the text color when a day is highlighted
- Different highlighting strength based on the number of events in each day
