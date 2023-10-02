import { defineConfig, presetUno, presetIcons, presetTypography } from 'unocss'

export default defineConfig({
    theme: {
        colors: {
            moon: {
                100: '#b6cbe7',
                200: '#6b95d2',
                300: '#4f76a7',
                400: '#43648d',
                500: '#3c597e',
                600: '#293d57',
                700: '#1f2e41',
                800: '#182433',
                900: '#151f2c'
            },
            navy: {
                100: '#d2dee4',
                200: '#a5bdc9',
                300: '#789cae',
                400: '#4b7b93',
                500: '#1e5a78',
                600: '#184860',
                700: '#123648',
                800: '#0c2430',
                900: '#061218'
            }
        }
    },
    presets: [
        presetUno(),
        presetIcons(),
        presetTypography({
            selectorName: 'prose-content',
            cssExtend: {
                'a:hover': {
                    color: 'rgba(245, 158, 11, var(--un-text-opacity));'
                },
                'a:visited': {
                    color: 'rgba(245, 158, 11, var(--un-text-opacity));'
                },
                'html:not(.dark) .prose-content :where(:not(pre) > code)': {
                    'background-color': '#ededeb',
                    color: '#2f2f2',
                    'padding-left': '0.25rem',
                    'padding-right': '0.25rem',
                    'padding-top': '0.4rem',
                    'padding-bottom': '0.125rem',
                    'border-radius': '0.25rem',
                    'font-family': 'monospace',
                    'font-weight': 'normal',
                    'letter-spacing': '0.05rem',
                    'font-size': '0.9rem'
                },
                'html.dark .prose-content :where(:not(pre) > code)': {
                    'background-color': '#41413f',
                    color: '#e9e9e9',
                    'padding-left': '0.25rem',
                    'padding-right': '0.25rem',
                    'padding-top': '0.4rem',
                    'padding-bottom': '0.125rem',
                    'border-radius': '0.25rem',
                    'font-family': 'monospace',
                    'font-weight': 'normal',
                    'letter-spacing': '0.05rem',
                    'font-size': '0.9rem'
                },
                '.prose-content :where(pre):not(:where(.not-prose-content,.not-prose-content *))': {
                    'padding-left': '0rem',
                    'padding-right': '0rem'
                },
                '.prose-content :where(:not(pre) > code)::before': {
                    content: '"" !important'
                },
                '.prose-content :where(:not(pre) > code)::after': {
                    content: '"" !important'
                }
            }
        })
    ],
    shortcuts: {
        anchor: 'py-4 text-gray-800 dark:text-gray-200',
        'anchor-underline': 'bg-gray-800 dark:bg-gray-200',
        'header-anchor': 'text-gray-800 dark:text-gray-200',
        article: `border border-solid border-[1px] rounded border-gray-200 dark:border-gray-800 
                    bg-white dark:bg-gray-800 
                    w-fit px-8 my-8 flex justify-center`
    }
})
