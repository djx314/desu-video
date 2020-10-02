// import styled from '@emotion/styled'
// import { css } from '@emotion/core'

// export const HeightStyle = css({
//   minHeight: '100vh'
// })

// export const GStyle = styled.div`
//   .trigger {
//     font-size: 18px;
//     line-height: 64px;
//     padding: 0 24px;
//     cursor: pointer;
//     transition: color 0.3s;
//   }

//   .trigger:hover {
//     color: #1890ff;
//   }

//   .logo {
//     height: 32px;
//     background: rgba(255, 255, 255, 0.2);
//     margin: 16px;
//   }

//   .site-layout .site-layout-background {
//     background: #fff;
//   }
// `


import styled from '@emotion/styled'
import { css } from '@emotion/core'

export const HeightStyle = css({
  minHeight: "100vh"
})

export const WhiteBlackground = css({
  background: "#fff"
})

export const TextCenter = css({
  textAlign: "center"
})

export const HeaderStyle = css({
  padding: 0
}, WhiteBlackground)

export const ContentStyle = css({
  margin: "24px 16px",
  padding: 24,
  minHeight: 280
}, WhiteBlackground)

export const LogoStyle = css({
  height: 32,
  background: "rgba(255, 255, 255, 0.2)",
  margin: 16
})

export const GStyle = styled.div`
  .trigger {
    font-size: 18px;
    line-height: 64px;
    padding: 0 24px;
    cursor: pointer;
    transition: color 0.3s;
  }
  
  .trigger:hover {
    color: #1890ff;
  }
`
