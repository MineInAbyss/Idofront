# About

[:brand-github: GitHub](https://github.com/MineInAbyss/Idofront)

Idofront is a set of modules we share between plugins. It includes helpful Minecraft extensions, gradle conventions, and
more.

To use it in your plugins see the GitHub page for depending on Idofront in your project, download the latest jar and
include it in your `paper-plugin.yml`:

```yaml
dependencies:
  server:
    Idofront:
      required: true
      load: BEFORE
      join-classpath: true
```
