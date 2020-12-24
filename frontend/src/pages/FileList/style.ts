import { css } from '@emotion/core'
import styled from '@emotion/styled'

export const bodyContent = (bodyHeight: number) => {
    return css({
        height: bodyHeight,
        backgroundColor: "red"
    })
}

export const ContentStyle = styled.div`
    padding: 10px;
`