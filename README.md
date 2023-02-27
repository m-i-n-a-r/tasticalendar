# TastiCalendar

A simple library, based on Material You, to implement a monthly or yearly calendar containing a collection of dates

<p align='center'>
  <a href='https://github.com/m-i-n-a-r/birday/blob/master/LICENSE.md'><img src='https://img.shields.io/badge/license-GPL 3-333333'/></a>
  <img src='https://img.shields.io/github/v/release/m-i-n-a-r/tasticalendar'>
</p>

## Introduction
I wrote this library starting from a piece of [Birday](https://www.github.com/m-i-n-a-r/birday), since i noticed that there isn't a similar library (at least, not a recent one). I kept it super simple and light, but I'm open to any pull request. 
**Important**: this library is provided as is, no updates are guaranteed since i have other projects to focus on. It works (i use it personally in 2 projects) and is quite complete in my opinion, but I'm open to any criticism. This library doesn't need any translation since it doesn't use any string itself.

## How to use
Just open the ```build.gradle (Module:app)``` file of your app, and under dependencies, add:

``` implementation 'com.github.m-i-n-a-r:tasticalendar:1.0.0' ```
Further instructions and examples will come with future releases, since there's still something to add. 

## Features
<img src='https://i.imgur.com/Zc0exjT.png' width='18%'/>

- Super lightweight
- The color scheme automatically adapts to the app (Material You / Monet)
- Month layout (with different scales)
- Year layout (adaptive depending on the month scale factor)
- Clickable days, clickable month titles
- Easily set any property or render a different year/month
- Easily pass a collection of dates or TastiCalendarEvent objects to highlight a set of dates on a month
- The library chooses the best contrast for the text color when a day is highlighted
- Different highlighting strength based on the number of events in each day
