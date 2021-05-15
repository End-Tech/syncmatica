# Syncmatica Configuration Manual

This document explains what the possibilities, options and restrictions of configuring syncmatica are.

## Server Side

On the server syncmatica is configured by a `config.json` which can be found in `config/syncmatica/config.json`. The
json inside configures several settings on the Server which are grouped together into categories. Here is an example of
the current (As of this files last update time current) `config.json`:

```json
{
	"quota": {
		"enabled": false,
		"limit": 40000000
	}
}
```

If the file is missing or otherwise cannot be read, the file might completely reset itself during startup. If parts of
the configuration are damaged/unreadable, the file will reset the portions during startup. Extra entries are ignored.

### Quota

The key "quota" configures the quota feature of the server.

* `enabled` defines whether the quota feature is enabled on the server. The feature blocks uploads from clients if the
  client exceeds a limit for file uploads. How much the client already uploaded resets itself when the server shuts
  down. Can be `true` or `false`
* `limit` defines the limit that a client is able to upload in bytes.
