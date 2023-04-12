package com.ddockddack.domain.game.controller;

import com.ddockddack.domain.game.request.GameModifyReq;
import com.ddockddack.domain.game.request.GameSaveReq;
import com.ddockddack.domain.game.response.GameDetailRes;
import com.ddockddack.domain.game.response.GameRes;
import com.ddockddack.domain.game.service.GameService;
import com.ddockddack.domain.report.entity.ReportType;
import com.ddockddack.global.oauth.MemberDetail;
import com.ddockddack.global.util.PageConditionReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameApiController {

    private final GameService gameService;

    @GetMapping()
    @Operation(summary = "게임 목록 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 목록 조회 성공")
    })
    public ResponseEntity<PageImpl<GameRes>> gameList(
        @ModelAttribute PageConditionReq pageConditionReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        Long memberId = null;
        if (memberDetail != null) {
            memberId = memberDetail.getId();
        }
        PageImpl<GameRes> allGames = gameService.findAllGames(memberId, pageConditionReq);

        return ResponseEntity.ok(allGames);
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "게임 상세조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임")
    })
    public ResponseEntity<GameDetailRes> gameDetails(@PathVariable Long gameId) {

        return ResponseEntity.ok(gameService.findGame(gameId));
    }

    @PostMapping
    @Operation(summary = "게임 생성")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 생성 성공"),
        @ApiResponse(responseCode = "400", description = "필수 값 누락"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저"),
        @ApiResponse(responseCode = "413", description = "파일 용량 초과"),
        @ApiResponse(responseCode = "414", description = "입력 범위 벗어남"),
        @ApiResponse(responseCode = "414", description = "지원 하지 않는 확장자")
    })
    public ResponseEntity gameSave(@ModelAttribute @Valid GameSaveReq gameSaveReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        gameService.saveGame(memberDetail.getId(), gameSaveReq);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/{gameId}")
    @Operation(summary = "게임 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 수정 성공"),
        @ApiResponse(responseCode = "400", description = "필수 값 누락"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "413", description = "파일 용량 초과"),
        @ApiResponse(responseCode = "414", description = "지원 하지 않는 확장자")
    })
    public ResponseEntity gameModify(@ModelAttribute @Valid GameModifyReq gameModifyReq,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        gameService.modifyGame(memberDetail, gameModifyReq);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{gameId}")
    @Operation(summary = "게임 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameRemove(@PathVariable Long gameId,
        @AuthenticationPrincipal MemberDetail memberDetail) {

        gameService.removeGame(memberDetail, gameId);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/starred/{gameId}")
    @Operation(summary = "게임 즐겨 찾기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 즐겨찾기 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameStarred(@PathVariable Long gameId,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        gameService.starredGame(memberDetail.getId(), gameId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unstarred/{gameId}")
    @Operation(summary = "게임 즐겨 찾기 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 즐겨찾기 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameUnStarred(@PathVariable Long gameId,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        gameService.unStarredGame(memberDetail.getId(), gameId);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/report/{gameId}")
    @Operation(summary = "게임 신고")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "게임 신고 성공"),
        @ApiResponse(responseCode = "400", description = "이미 존재하는 신고"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 게임"),
        @ApiResponse(responseCode = "404", description = "존재 하지 않는 유저")
    })
    public ResponseEntity gameReport(@PathVariable Long gameId,
        @RequestBody Map<String, String> body,
        @AuthenticationPrincipal MemberDetail memberDetail) {
        gameService.reportGame(memberDetail.getId(), gameId,
            ReportType.valueOf(body.get("reportType")));
        return ResponseEntity.ok().build();

    }

}
