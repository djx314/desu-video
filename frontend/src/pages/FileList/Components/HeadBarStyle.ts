import { css } from '@emotion/core'

export const barContent = (barHeight: number) => {
    return css({
        height: barHeight,
        backgroundColor: "rgb(30, 30, 30)",
        paddingTop: barHeight * 0.1,
        lineHeight: "normal",
        fontSize: `${barHeight * 0.58}px`,
        paddingLeft: 20,
        color: "rgb(233, 233, 233)"
    })
}