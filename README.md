# flextimemonitor

A small Android app for keeping track of flex time hours without having to wait for HR table update.

## TODO in v0.0.4
- Get the working days in a month.
- Show the due hours in this month, at the start of the month.
- Save previous month events in a monthly database.
- Proper action bar usage.
- Set fixed initial flex time.
- Use day class to stablish the state of the chronometer.
- Deal with absent state when the first check in is after 10am.

## v0.0.4
- Average each previous day into a single list element.
- Count just the Flex Time Hours.

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

