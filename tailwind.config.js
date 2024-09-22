/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/main/resources/templates/**/*.html', // Thymeleaf templates
    './src/**/*.java'  // Optional, if you want to scan Java files too
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}


