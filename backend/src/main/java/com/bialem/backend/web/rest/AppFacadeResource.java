package com.bialem.backend.web.rest;

import com.bialem.backend.service.AppMediaService;
import com.bialem.backend.service.AppQueryService;
import com.bialem.backend.service.AppRpcService;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.web.rest.vm.AppQueryRequest;
import com.bialem.backend.web.rest.vm.AppQueryRequest.AppQueryResponse;
import java.nio.file.Files;
import java.util.Map;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/app")
public class AppFacadeResource {

    private final AppQueryService queryService;
    private final AppRpcService rpcService;
    private final AppMediaService mediaService;
    private final AppSupport support;

    public AppFacadeResource(
        AppQueryService queryService,
        AppRpcService rpcService,
        AppMediaService mediaService,
        AppSupport support
    ) {
        this.queryService = queryService;
        this.rpcService = rpcService;
        this.mediaService = mediaService;
        this.support = support;
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        return support.profileEmbed(support.currentProfile());
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe() {
        var profile = support.currentProfile();
        profile.setStatus(com.bialem.backend.domain.enumeration.ProfileStatus.DELETED);
        profile.getUser().setActivated(false);
    }

    @PostMapping("/query")
    public AppQueryResponse query(@RequestBody AppQueryRequest request) {
        return queryService.execute(request);
    }

    @PostMapping("/rpc/{name}")
    public AppQueryResponse rpc(@PathVariable String name, @RequestBody(required = false) Map<String, Object> args) {
        return rpcService.invoke(name, args);
    }

    @PostMapping("/ai/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> body) {
        return Map.of(
            "reply",
            "Bialem Asistan artık kendi backend'iniz üzerinden çalışıyor. Topluluk, etkinlik ve keşif ekranlarından devam edebilirsiniz."
        );
    }

    @PostMapping(path = "/media/{bucket}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> upload(
        @PathVariable String bucket,
        @RequestParam("path") String path,
        @RequestParam("file") MultipartFile file
    ) {
        return mediaService.save(bucket, path, file);
    }

    @DeleteMapping("/media/{bucket}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedia(@PathVariable String bucket, @RequestParam("path") String path) {
        mediaService.delete(bucket, path);
    }

    @GetMapping("/media-proxy")
    public ResponseEntity<byte[]> mediaProxy(@RequestParam("url") String url) {
        return mediaService.proxyRemote(url);
    }

    @GetMapping("/media/{bucket}/{*path}")
    public ResponseEntity<Resource> media(@PathVariable String bucket, @PathVariable String path) throws Exception {
        var file = mediaService.resolve(bucket, path);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(file);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType == null ? "application/octet-stream" : contentType))
            .body(new FileSystemResource(file));
    }
}
