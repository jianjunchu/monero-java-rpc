package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import daemon.MoneroDaemon;
import daemon.model.MoneroBlockCount;
import daemon.model.MoneroBlockHeader;
import daemon.model.MoneroBlockTemplate;
import daemon.model.MoneroDaemonModel;
import utils.TestUtils;

/**
 * Tests a Monero daemon.
 */
public class TestMoneroDaemon {
  
  private static MoneroDaemon daemon;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    daemon = TestUtils.getDaemon();
  }

  @Test
  public void testGetBlockCount() {
    MoneroBlockCount blockCount = daemon.getBlockCount();
    testDaemonStatus(blockCount, true, false);
    assertNotNull(blockCount.getCount());
    assertTrue(blockCount.getCount().intValue() > 0);
  }

  @Test
  public void testGetBlockHash() {
    MoneroBlockHeader lastHeader = daemon.getLastBlockHeader();
    String hash = daemon.getBlockHash(lastHeader.getHeight());
    assertNotNull(hash);
    assertEquals(64, hash.length());
  }

  @Test
  public void testGetBlockTemplate() {
    MoneroBlockTemplate template = daemon.getBlockTemplate("49hNury7mhADaH5r4buqRK9Mt5yFSPZdHanYcZzuNUV1fGRoVABHpnd81bUdTSZk5MaCb5ptsVSVAEusrmr61iX83FFTAuF", 2);
    testDaemonStatus(template, true, true);
    testBlockTemplate(template);
  }

  @Test
  public void testGetLastBlockHeader() {
    MoneroBlockHeader lastHeader = daemon.getLastBlockHeader();
    testDaemonStatus(lastHeader, true, true);
    testBlockHeader(lastHeader);
  }

  @Test
  public void testGetBlockHeaderByHash() {
    
    // retrieve by hash of last block
    MoneroBlockHeader lastHeader = daemon.getLastBlockHeader();
    String hash = daemon.getBlockHash(lastHeader.getHeight());
    MoneroBlockHeader header = daemon.getBlockHeader(hash);
    testDaemonStatus(header, true, true);
    testBlockHeader(header);
    assertEquals(lastHeader, header);
    
    // retrieve by hash of previous to last block
    hash = daemon.getBlockHash(lastHeader.getHeight() - 1);
    header = daemon.getBlockHeader(hash);
    testDaemonStatus(header, true, true);
    testBlockHeader(header);
    assertEquals((int) lastHeader.getHeight() - 1, (int) header.getHeight()); 
  }

  @Test
  public void testGetBlockHeaderByHeight() {
    
    // retrieve by height of last block
    MoneroBlockHeader lastHeader = daemon.getLastBlockHeader();
    MoneroBlockHeader header = daemon.getBlockHeader(lastHeader.getHeight());
    testDaemonStatus(header, true, true);
    testBlockHeader(header);
    assertEquals(lastHeader, header);
    
    // retrieve by height of previous to last block
    header = daemon.getBlockHeader(lastHeader.getHeight() - 1);
    testDaemonStatus(header, true, true);
    testBlockHeader(header);
    assertEquals((int) lastHeader.getHeight() - 1, (int) header.getHeight()); 
  }

  @Test
  public void testGetBlockHeaders() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetBlockString() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetBlockInt() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetConnections() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetInfo() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetSyncInfo() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetHardForkInfo() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetBan() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetBans() {
    fail("Not yet implemented");
  }

  @Test
  public void testFlushTxPool() {
    fail("Not yet implemented");
  }

  @Test
  public void testFlushTxPoolCollectionOfString() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetOutputHistogram() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetOutputDistribution() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetCoinbaseTxSum() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetFeeEstimate() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetAlternativeChains() {
    fail("Not yet implemented");
  }

  @Test
  public void testRelayTx() {
    fail("Not yet implemented");
  }

  @Test
  public void testRelayTxs() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetTxPoolBacklog() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetAltBlockHashes() {
    fail("Not yet implemented");
  }

  @Test
  public void testIsKeyImageSpent() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetTxs() {
    fail("Not yet implemented");
  }

  @Test
  public void testStartMining() {
    fail("Not yet implemented");
  }

  @Test
  public void testStopMining() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetMiningStatus() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetBandwidthLimit() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetBandwidthLimit() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetNumOutgoingLimit() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetNumIncomingLimit() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testSubmitBlock() {
    fail("Not yet implemented");
  }

  private static void testDaemonStatus(MoneroDaemonModel model, boolean initializedStatus, boolean initializedIsUntrusted) {
    if (initializedStatus) {
      assertEquals("OK", model.getStatus());
    } else {
      assertNull(model.getStatus());
    }
    if (initializedIsUntrusted) {
      assertNotNull(model.isTrusted());
    } else {
      assertNull(model.isTrusted());
    }
  }
  
  private static void testBlockHeader(MoneroBlockHeader header) {
    assertNotNull(header.getBlockSize());
    assertNotNull(header.getDepth());
    assertNotNull(header.getDepth());
    assertNotNull(header.getDifficulty());
    assertNotNull(header.getHash());
    assertNotNull(header.getHeight());
    assertNotNull(header.getMajorVersion());
    assertNotNull(header.getMinorVersion());
    assertNotNull(header.getNonce());
    assertNotNull(header.getNumTxs());
    assertNotNull(header.getOrphanStatus());
    assertNotNull(header.getPrevHash());
    assertNotNull(header.getReward());
    assertNotNull(header.getTimestamp()); 
  }
  
  private static void testBlockTemplate(MoneroBlockTemplate template) {
    assertNotNull(template.getTemplateBlob());
    assertNotNull(template.getHashBlob());
    assertNotNull(template.getDifficulty());
    assertNotNull(template.getExpectedReward());
    assertNotNull(template.getHeight());
    assertNotNull(template.getPrevHash());
    assertNotNull(template.getReservedOffset());
    
  }
}