import js from "@eslint/js";
import globals from "globals";
import vue from "eslint-plugin-vue";

export default [
  {
    ignores: ["dist/**", "../resources/static/**", "coverage/**"]
  },
  js.configs.recommended,
  ...vue.configs["flat/essential"],
  {
    files: ["src/**/*.{js,vue}"],
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      globals: {
        ...globals.browser,
        ...globals.node
      }
    },
    rules: {
      "vue/multi-word-component-names": "off",
      "vue/no-v-html": "off"
    }
  },
  {
    files: ["src/**/*.{test,spec}.js", "src/test/**/*.js"],
    languageOptions: {
      globals: {
        ...globals.vitest
      }
    }
  }
];
