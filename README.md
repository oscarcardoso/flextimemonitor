# flextimemonitor

A small Android app for keeping track of flex time hours without having to wait for HR table update.

## TODO in v0.0.5
- Setup the Flex2 schedule: Saturdays count as a regular day, lunch time is just half an hour.
- Setup the Edit Mode View.
- Add buttons to delete or modify day/event status.
- Restructure the database access procedure to show a loading screen and make load/read more efficiently.
- Add a status bar notification for quick access. (With check action if possible).

## TODO in v0.0.4
- Add, remove and edit a Day Off (vacations, special permits, days off, reposition days).
- Get the working days in a month.
- Show the due hours in this month, at the start of the month.
- Make a Ivan-Fix: Count the absent days as todaysTime - fixedTime - lateTime
- Set fixed initial flex time.
- Save previous month events in a monthly database.
- Sort events when loading for group event.

## v0.0.4
- Average each previous day into a single list element.
- Count just the Flex Time Hours.
- Deal with absent state when the first check in is after 10am.
- Use day class to stablish the state of the chronometer.
- Proper action bar usage.
- MainActivity List shows only this month events.

## v0.0.3 STABLE
- Update each check event individually.
- Export database to CSV.
- Show todays covered hours in a timer right next to 
  the months timer.
- Delete elements by onHold event in the Events list.
- UI Improvements.
- Forced portrait layout for now.
- Delete previous month events. (For testing purposes)

### v0.0.2
- New version number convention (major, minor, point).
- SQLite implementation.
- Save the Checked In time in a file for quick access.
- Green generic Icon.
- Display previous checks in lower part of layout.
- Properly handle default events (onPause, onResume, onStop, onClick)
- General bug fixes and has now achieved functional status.

### v0.1
- First version!
- Big default buttons with holo theme.
- Versioning ready and up in GitHub.
- Use a timer with a separate thread.
- Start and stop timer when entering and exiting the app
- Show an about Toast when selecting "About" on the overflow menu

