import { css } from '@emotion/core'
import styled from '@emotion/styled'

export const titleHeight = 30

export const TitleStyle = css({
    textAlign: "center",
    height: titleHeight
})

export const ImageContent = (nHeight: number) => css({
    height: nHeight
})

export const ImageContent1 = css({
    display: "flex",
    flexFlow: "column",
    height: "100%"
})

export const ImageElement = css({
    display: "flex",
    height: `${100 / 4}%`
})

export const ImageElement1 = css({
    display: "block",
    width: `${100 / 5}%`,
    height: `100%`
})

export const ImageStyle = css({
    display: "block",
    margin: "auto auto",
    maxWidth: "100%",
    maxHeight: "100%"
})

export const GStyle = styled.div`
    display: flex;
    flex-flow: column;
    height: 100%;
`