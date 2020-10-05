import { css } from '@emotion/core'

export const barContent = (barHeight: number) => {
    return css({
        height: barHeight,
        backgroundColor: "rgb(50, 50, 51)",
        paddingLeft: 20
    })
}

export const fileListComp = (compHeight: number) => {
    return css({
        lineHeight: "normal",
        paddingTop: compHeight * 0.1,
        paddingBottom: compHeight * 0.1,
        fontSize: `${compHeight * 0.6}px`,
        color: "rgb(233, 233, 233)"
    })
}