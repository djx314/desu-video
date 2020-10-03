import { css } from '@emotion/core'

export const barContent = (barHeight: number) => {
    return css({
        height: barHeight,
        backgroundColor: "rgb(50, 50, 51)",
        lineHeight: `${barHeight}px`,
        fontSize: `${barHeight * 0.6}px`,
        paddingLeft: 20,
        color: "rgb(233, 233, 233)"
    })
}