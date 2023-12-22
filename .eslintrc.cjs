module.exports = {
  root: true,
  extends: [
    "@react-native-community",
    "plugin:react-native-a11y/ios",
    "prettier",
  ],
  parser: "@typescript-eslint/parser",
  plugins: ["@typescript-eslint"],
  overrides: [
    {
      env: {
        jest: true,
      },
      files: ["*.ts", "*.tsx", "**/*.e2e.js"],
      rules: {
        "no-shadow": "off",
        "no-undef": "off",
      },
    },
  ],
};
