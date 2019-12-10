## [unreleased]

## [0.7.0] - 2019-12-10
### Added
- `gson` maven dependency added
- `javax.websocket-api` maven dependency added
- `tyrus-standalone-client` maven dependency added
- `WSClientEndpoint` helper class (moved from GlobalIntercom)
- `SQLite.getRawDatabase` to get raw Database object
- README file
### Fixed
- check if db is not null on `SQLite`
### Changed
- [breaking] `PluginChangeWatcher` is now static with only one `WatchService` and one `WatchThread` for all `FileChangeListener` resulting in less threads opened.

## [0.6.0] - 2019-04-13
### Added
- Merged Miwarre's rwgui plugin into this lib as drop-in-replacement

## [0.5.0] - 2019-04-06
### Added
- Wrapper class for SQLite Database connection

## [0.4.1] - 2019-03-05
### Fixed
- Colors class getInstance was not static

## [0.4.0] - 2019-01-31
### Added
- Colors singleton class to provide streamlined colors for different plugins

## [0.3.0] - 2019-01-28
### Added
- new method `getLanguageAvailable` shows all language files loaded
### Changed
- the method `getLanguageUsed` now adds a hint when default language is choosen

## [0.2.0] - 2019-01-25
### Added
- method to get the effective language I18n->getLanguageUsed(String language)
- HISTORY.en.md

## [0.1.1]
## [0.1.0]
